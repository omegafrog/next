import { NextRequest, NextResponse } from "next/server";
import client from "./lib/backend/apiV1/fetchClient";
import { cookies } from "next/headers";

// 항상 토큰을 재발급요청하지 말고
// 토큰이 만료되었고 로그인한 상태일 때만 재발급 요청을 보내자
export async function middleware(request: NextRequest) {
  // 쿠키로부터 토큰 얻기
  const { payload, isExpired, isLogin } = await getAccessTokenFromCookies();

  console.log("------------------");
  console.log(isLogin, isExpired);

  // 로그인 상태이고 토큰 만료시 재발급
  if (isLogin && isExpired) {
    const nextResponse = NextResponse.next();

    // 서버로부터 토큰 재발급 받음.
    // 재발급 받은 쿠키는 set-cookie에 들어가 있을 것
    const response = await client.GET("/api/v1/members/me", {
      headers: {
        cookie: (await cookies()).toString(),
      },
    });

    // 발급받은 쿠키를 set-cookie헤더에서 가져와
    // next->client로 가는 response에 set-cookie 헤더 설정정
    const springCookie = response.response.headers.getSetCookie();

    nextResponse.headers.set("set-cookie", String(springCookie));
    return nextResponse;
  }

  // 로그인이 되지 않은 상태에서 수정과 작성글은 아예 접근이 불가해야 하고
  // 나머지는 접근이 가능해야 하므로 이를 필터링
  if (!isLogin && isProtectedRoute(request.nextUrl.pathname)) {
    return createUnauthorizedResponse();
  }

  async function getAccessTokenFromCookies() {
    const myCookies = await cookies();
    const accessToken = myCookies.get("accessToken");

    let isExpired = true;
    let payload = null;
    let isLogin = false;
    if (accessToken) {
      // 토큰 파싱
      try {
        const tokenParts = accessToken.value.split(".");
        payload = JSON.parse(Buffer.from(tokenParts[1], "base64").toString());
        const expTimestamp = payload.exp * 1000; // exp는 초 단위이므로 밀리초로 변환
        isExpired = Date.now() > expTimestamp;
        console.log("토큰 만료 여부:", isExpired);
        isLogin = payload !== null;

      } catch (e) {
        console.error("토큰 파싱 중 오류 발생:", e);
      }
    }
    return { payload, isExpired, isLogin };
  }
}
function isProtectedRoute(pathname: string): boolean {
  return (
    pathname.startsWith("/post/write") || pathname.startsWith("/post/edit")
  );
}

function createUnauthorizedResponse(): NextResponse {
  return new NextResponse("로그인이 필요합니다.", {
    status: 401,
    headers: {
      "Content-Type": "text/html; charset=utf-8",
    },
  });
}
export const config = {
  matcher: "/((?!.*\\.|api\\/).*)",
};
