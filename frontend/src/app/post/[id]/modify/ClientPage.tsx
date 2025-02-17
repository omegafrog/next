"use client";

import client from "@/lib/backend/apiV1/fetchClient";
import { components } from "@/lib/backend/apiV1/schema";
import { useRouter } from "next/navigation";

export default function ClientPage({
  post,
}: {
  post: components["schemas"]["PostWithContentDto"];
}) {
  const router = useRouter();
  async function modify(e: React.FormEvent) {
    e.preventDefault();

    const target = e.target as HTMLFormElement;
    const title = target._title.value;
    const content = target.content.value;
    const opened = target.opened.checked;
    const listed = target.listed.checked;

    const response = await client.PUT("/api/v1/posts/{id}", {
      params: {
        path: {
          id: post.id,
        },
      },
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

    const modified = response.data.data;

    router.push(`/post/${modified.id}`);
  }
  return (
    <>
      <h1>글 수정 페이지</h1>

      <form onSubmit={modify} className="flex flex-col w-1/4 gap-3">
        <div className="flex-grow">
          <label>공개 여부</label>
          <input type="checkbox" name="opened" defaultChecked={post.opened} />
          <label>검색 여부</label>
          <input type="checkbox" name="listed" defaultChecked={post.listed} />
        </div>
        <input
          type="text"
          name="_title"
          placeholder="제목 입력"
          className="border-2 border-black"
          defaultValue={post.title}
        />
        <textarea
          name="content"
          className="border-2 border-black"
          defaultValue={post.content}
        ></textarea>
        <input type="submit" value="등록" />
      </form>
    </>
  );
}
