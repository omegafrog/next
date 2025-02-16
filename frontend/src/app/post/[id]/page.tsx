import createClient from "openapi-fetch";
import ClientPage from "./ClientPage";
import { paths } from "@/lib/backend/apiV1/schema";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

export default async function Page({
  params,
}: {
  params: {
    id: number;
  };
}) {
  const { id } = await params;

  const response = await client.GET("/api/v1/posts/{id}", {
    params: {
      path: {
        id,
      },
    },
  });

  const rsData = response.data!;
  if (response.error) {
    console.log(response);
    return <div>존재하지 않는 페이지입니다.</div>;
  }
  const post = rsData.data;
  return <ClientPage post={post} />;
}
