"use client";

import client from "@/lib/backend/apiV1/fetchClient";
import { useRouter } from "next/navigation";

export default function ClinetPage() {
  const router = useRouter();
  async function write(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const target = e.target as HTMLFormElement;
    const title = target._title.value;
    const content = target.content.value;
    const opened = target.opened.checked;
    const listed = target.listed.checked;

    if (title.trim().length === 0) {
      alert("제목을 입력해주세요.");
      return;
    }

    if (content.trim().length === 0) {
      alert("내용을 입력해주세요.");
      return;
    }

    const response = await client.POST("/api/v1/posts", {
      body: {
        title,
        content,
        opened,
        listed,
      },
    });
    if (response.error) {
      console.log(response);
      return <div>{response.error.msg}</div>;
    }
    const post = response.data.data;
    router.push(`/post/${post.id}`);
  }
  return (
    <>
      <h1>글 작성 페이지</h1>

      <form onSubmit={write} className="flex flex-col w-1/4 gap-3">
        <div className="flex-grow">
          <label>공개 여부</label>
          <input type="checkbox" name="opened" />
          <label>검색 여부</label>
          <input type="checkbox" name="listed" />
        </div>
        <input
          type="text"
          name="_title"
          placeholder="제목 입력"
          className="border-2 border-black"
        />
        <textarea name="content" className="border-2 border-black"></textarea>
        <input type="submit" value="등록" />
      </form>
    </>
  );
}
