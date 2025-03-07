import type { Metadata } from "next";
import { Geist, Geist_Mono, Linden_Hill } from "next/font/google";
import "./globals.css";
import Link from "next/link";
import ClinetLayout from "./ClientLayout";
import client from "@/lib/backend/apiV1/fetchClient";
import { cookies } from "next/headers";
import { components } from "@/lib/backend/apiV1/schema";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Create Next App",
  description: "Generated by create next app",
};

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const response = await client.GET("/api/v1/members/me", {
    headers: {
      cookie: (await cookies()).toString(),
    },
  });
  const me: components["schemas"]["MemberDto"] = response.data
    ? response.data.data
    : {
        id: 0,
        nickname: "",
        createdDate: "",
        modifiedDate: "",
      };
  return <ClinetLayout me={me}>{children}</ClinetLayout>;
}
