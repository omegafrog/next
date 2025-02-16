import ClientPage from "./ClientPage";
import client from "@/lib/backend/apiV1/fetchClient";

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
    return <div>{response.error.msg}</div>;
  }
  const post = rsData.data;
  return <ClientPage post={post} />;
}
