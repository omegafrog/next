"use client";
import { Button, buttonVariants } from "@/components/ui/button";
import client from "@/lib/backend/apiV1/fetchClient";
import { components } from "@/lib/backend/apiV1/schema";
import Link from "next/link";
export default function ClinetLayout({
  children,
  me,
  fontVariable,
  fontClassName
}: Readonly<{
  children: React.ReactNode;
  me: components["schemas"]["MemberDto"],
  fontVariable: string,
  fontClassName: string
}>) {
  const isLoggedIn = me.id !== 0;
  return (
    <html lang="en" className={`${fontVariable}`}>
      <body className={`min-h-[100dvh] flex flex-col ${fontClassName}`}>
        <header className="flex gap-3">
          <Link href="/">메인</Link>
          <Link href="/about">소개</Link>
          <Link href="/post/list">글 목록</Link>
          {isLoggedIn && <Link className={buttonVariants({ variant: "outline" })} href="/post/write">글 작성</Link>}
          {!isLoggedIn && <Link href="/member/login">로그인</Link>}
          {!isLoggedIn && <Link href="/member/register">회원가입</Link>}
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
