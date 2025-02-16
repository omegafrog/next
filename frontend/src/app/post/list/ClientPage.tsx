"use client";

import Link from "next/link";
import { components } from "@/lib/backend/apiV1/schema";
import { useRouter } from "next/navigation";

export default function ClientPage({
  rsData,
  keyword,
  keywordType,
  size,
}: {
  rsData: components["schemas"]["RsDataPageDto"];
  keyword: string;
  keywordType: string;
  size: number;
}) {
  const pageDto = rsData.data;

  const router = useRouter();
  return (
    <>
      <div>
        <h1>글 목록</h1>
        <div>응답 코드 : {rsData.code}</div>
        <div>결과 메시지 : {rsData.msg}</div>
        <div>totalPages : {pageDto.totalPageNum}</div>
        <div>totalItems : {pageDto.totalElementSize}</div>
        <div>currentPageNo : {pageDto.currentPageNum}</div>
        <div>pageSize : {pageDto.pageSize}</div>

        <form
          onSubmit={(e) => {
            e.preventDefault();

            const formData = new FormData(e.target as HTMLFormElement);
            const searchKeyword = formData.get("keyword") as string;
            const keywordType = formData.get("keywordType") as string;
            const page = 0;
            const pageSize = formData.get("size") as string;

            router.push(
              `/post/list?keywordType=${keywordType}&keyword=${searchKeyword}&page=${page}&size=${pageSize}`
            );
          }}
        >
          <label className="ml-5" htmlFor="">
            페이지당 행 개수 :
          </label>
          <select name="size" defaultValue={10}>
            <option value="10">10</option>
            <option value="30">30</option>
            <option value="50">50</option>
          </select>
          <select name="keywordType">
            <option value="title">제목</option>
            <option value="content">내용</option>
          </select>
          <input
            placeholder="검색어 입력"
            type="text"
            name="keyword"
            defaultValue={keyword}
          />
          <input type="submit" value="검색"></input>
        </form>
        <div className="flex gap-3">
          {Array.from({ length: pageDto.totalPageNum! }, (key, i) => i + 1).map(
            (page) => {
              return (
                <Link
                  key={page}
                  className={page == page ? `text-red-500` : `text-blue-500`}
                  href={`/post/list?keywordType=${keywordType}&keyword=${keyword}&size=${size}&page=${
                    page - 1
                  }`}
                >
                  {page - 1}
                </Link>
              );
            }
          )}
        </div>
        <hr />
        <ul>
          {pageDto.items?.map((item) => {
            return (
              <Link key={item.id} href={`/post/${item.id}`}>
                <li className="border-2 border-red-500 my-2 p-2">
                  <div>id : {item.id}</div>
                  <div>title : {item.title}</div>
                  <div>authorId : {item.authorId}</div>
                  <div>authorName : {item.authorName}</div>
                  <div>published : {item.opened ? "true" : "false"}</div>
                  <div>listed : {item.listed ? "true" : "false"}</div>
                </li>
              </Link>
            );
          })}
        </ul>
      </div>
    </>
  );
}
