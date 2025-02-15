import { paths } from "@/lib/backend/apiV1/schema";
import createClient from "openapi-fetch";
import ClientPage from "./ClientPage";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

export default async function PostList({
  searchParams,
}: {
  searchParams: {
    keywordType: string;
    keyword: string;
    page: number;
    size: number;
  };
}) {
  const {
    keywordType = "title",
    keyword = "",
    page = 1,
    size = 10,
  } = await searchParams;

  // const response = await fetch(
  //   `http://localhost:8080/api/v1/posts?keyword-type=${keywordType}&keyword=${keyword}`
  // );

  const response = await client.GET("/api/v1/posts", {
    params: {
      query: {
        keyword: keyword,
        keywordType: keywordType,
        page: page,
        size: size,
      },
    },
  });

  return (
    <ClientPage
      rsData={response.data!}
      keyword={keyword}
      keywordType={keywordType}
      size={size}
    />
  );
}
