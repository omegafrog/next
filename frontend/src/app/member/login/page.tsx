"use client";

import client from "@/lib/backend/apiV1/fetchClient";
import { useRouter } from "next/navigation";

export default function Page() {
  const router = useRouter();
  async function login(e: React.FormEvent) {
    e.preventDefault();

    const formData = e.target as HTMLFormElement;
    const username = formData.username.value as string;
    const password = formData.password.value as string;

    const response = await client.POST("/api/v1/members/login", {
      body: {
        username,
        password,
      },
    });

    if (response.error) {
      console.log(response);
      return <div>{response.error.msg}</div>;
    }
    router.push("/post/list");
  }
  return (
    <form onSubmit={login} className="flex flex-col w-1/4 gap-3">
      <input
        type="text"
        name="username"
        placeholder="아이디 입력"
        className="border-2 border-black"
      />
      <input
        type="password"
        name="password"
        placeholder="패스워드 입력"
        className="border-2 border-black"
      />
      <input type="submit" value="로그인" />
    </form>
  );
}
