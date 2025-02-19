import ClientPage from "./ClientPage";
import client from "@/lib/backend/apiV1/fetchClient";
import { components } from "@/lib/backend/apiV1/schema";
import { cookies } from "next/headers";

export default async function Page({
  params,
}: {
  params: {
    id: number;
  };
}) {
  const { id } = await params;
  const {  payload } = await getAccessTokenFromCookies();

  const nickname = payload.nickname;
  
  try {
    const post = await getPost(id);

    return <ClientPage post={post} meNickname={nickname} />;
  } catch (error) {
    console.log(error);
    if (typeof window !== "undefined") {
      alert(error);
      return;
    }
  }
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

async function getPost(
  id: number
): Promise<components["schemas"]["PostWithContentDto"]> {
  const response = await client.GET("/api/v1/posts/{id}", {
    params: {
      path: {
        id,
      },
    },
  });

  if (response.error) {
    throw new Error(response.error.msg);
  }

  return response.data.data;
}
