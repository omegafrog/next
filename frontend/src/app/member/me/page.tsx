import ClientPage from "./ClientPage";
import client from "@/lib/backend/apiV1/fetchClient";
import { cookies } from "next/headers";

export default async function Page() {
  const response = await client.GET("/api/v1/members/me", {
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  if (response.error) {
    console.log(response);
    return <div>{response.error.msg}</div>;
  }

  const me = response.data.data;

  return <ClientPage me={me} />;
}
