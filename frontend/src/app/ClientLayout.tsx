"use client";
import client from "@/lib/backend/apiV1/fetchClient";
import { components } from "@/lib/backend/apiV1/schema";
import Link from "next/link";
import { useRouter } from "next/navigation";
export default function ClinetLayout({
  children,
  me,
}: Readonly<{
  children: React.ReactNode;
  me: components["schemas"]["MemberDto"];
}>) {
  const router = useRouter();
  const isLoggedIn = me.id !== 0;
  return (
    <html lang="en">
      <body className="min-h-[100dvh] flex flex-col">
        <header className="flex gap-3">
          <Link href="/">메인</Link>
          <Link href="/about">소개</Link>
          <Link href="/post/list">글 목록</Link>
          {isLoggedIn && <Link href="/post/write">글 작성</Link>}
          {!isLoggedIn && <Link href="/member/login">로그인</Link>}
          {isLoggedIn && <Link href="/member/me">내 정보</Link>}
          {isLoggedIn && (
            <Link
              href=""
              onClick={async (e) => {
                e.preventDefault();
                const response = await client.DELETE("/api/v1/members/logout", {
                  credentials: "include",
                });
                if (response.error) {
                  alert(response.error.msg);
                  return;
                }
                window.location.href = "/post/list";
              }}
            >
              로그아웃
            </Link>
          )}
        </header>
        <div className="flex-grow">{children}</div>
        <footer>푸터</footer>
      </body>
    </html>
  );
}
