"use client";

import { components } from "@/lib/backend/apiV1/schema";
import Link from "next/link";

export default function ClientPage({
  post,
  meNickname,
}: {
  post: components["schemas"]["PostWithContentDto"];
  meNickname: string;
}) {
  console.log(meNickname, post);
  return (
    <>
      <div>
        <div>번호 : {post.id}</div>
        <div>제목 : {post.title}</div>
        <div>내용 : {post.content}</div>
        <div>등록일 : {post.createdDatetime}</div>
        <div>수정일 : {post.modifiedDatetime}</div>
        <div>공개 여부 : {post.opened ? "true" : "false"}</div>
        <div>리스팅 여부 : {post.listed ? "true" : "false"}</div>
      </div>
      <hr />
      {meNickname === post.authorName && (
        <Link href={`/post/${post.id}/modify`}>수정</Link>
      )}
    </>
  );
}
