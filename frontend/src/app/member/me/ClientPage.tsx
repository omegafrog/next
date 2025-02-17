"use client";

import { components } from "@/lib/backend/apiV1/schema";

export default function ClientPage({
  me,
}: {
  me: components["schemas"]["MemberDto"];
}) {
  return (
    <>
      <div>내정보 페이지</div>
      <div>번호 : {me.id}</div>
      <div>닉네임 : {me.nickname}</div>
      <div>가입일자 : {me.createdDate}</div>
      <div>수정일자 : {me.modifiedDate}</div>
    </>
  );
}
