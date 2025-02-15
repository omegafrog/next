import { paths } from "@/lib/backend/apiV1/schema";
import Link from "next/link";
import createClient from "openapi-fetch";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

export default async function PostList({
  searchParams,
}: {
  searchParams: { keywordType: string; keyword: string; page: number };
}) {
  const { keywordType = "title", keyword = "", page = 1 } = await searchParams;

  // const response = await fetch(
  //   `http://localhost:8080/api/v1/posts?keyword-type=${keywordType}&keyword=${keyword}`
  // );

  const response = await client.GET("/api/v1/posts", {
    params: {
      query: {
        keyword: keyword,
        keywordType: keywordType,
        page: page,
      },
    },
  });

  const rsData = response.data!;
  const pageDto = rsData.data!;
  console.log(pageDto.items);
  return (
    <div>
      <h1>글 목록</h1>
      <div>응답 코드 : {rsData.code}</div>
      <div>결과 메시지 : {rsData.msg}</div>
      <div>totalPages : {pageDto.totalPageNum}</div>
      <div>totalItems : {pageDto.totalElementSize}</div>
      <div>currentPageNo : {pageDto.currentPageNum}</div>
      <div>pageSize : {pageDto.pageSize}</div>

      <form>
        <label className="ml-5" htmlFor="">
          페이지당 행 개수 :
        </label>
        <select name="pageSize">
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
              <Link key={page} href={`/post/list?page=${page}`}>
                {page}
              </Link>
            );
          }
        )}
      </div>
      <hr />
      <ul>
        <li>글1</li>
        <li>글2</li>
        {pageDto.items?.map((item) => {
          return (
            <li className="border-2 border-red-500 my-2 p-2" key={item.id}>
              <div>id : {item.id}</div>
              <div>title : {item.title}</div>
              <div>authorId : {item.authorId}</div>
              <div>authorName : {item.authorName}</div>
              <div>published : {item.opened ? "true" : "false"}</div>
              <div>listed : {item.listed ? "true" : "false"}</div>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
