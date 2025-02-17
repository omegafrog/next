import { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
  console.log("middleware");
}

export const config = {
  matcher: "/:path*",
};
