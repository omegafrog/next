import ClientPage from "./ClientPage";
import client from "@/lib/backend/apiV1/fetchClient";
import { components } from "@/lib/backend/apiV1/schema";
import { cookies } from "next/headers";

export default async function Page({
  params,
}: {
  params: {
    id: number;
  };
}) {
  const { id } = await params;

  try {
    const post = await getPost(id);
    const me = await getMe();
    return <ClientPage post={post} me={me} />;
  } catch (error) {
    console.log(error);
    if (typeof window !== "undefined") {
      alert(error);
      return;
    }
  }
}

async function getMe(): Promise<components["schemas"]["MemberDto"]> {
  const response = await client.GET("/api/v1/members/me", {
    credentials: "include",
  });

  if (response.error) {
    return { id: 0 };
  }

  return response.data.data;
}

async function getPost(
  id: number
): Promise<components["schemas"]["PostWithContentDto"]> {
  const response = await client.GET("/api/v1/posts/{id}", {
    params: {
      path: {
        id,
      },
    },
  });

  if (response.error) {
    throw new Error(response.error.msg);
  }

  return response.data.data;
}
