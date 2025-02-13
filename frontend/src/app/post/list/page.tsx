export default async function PostList() {
  const response = await fetch("http://localhost:8080/api/v1/posts");

  if (!response.ok) {
    throw new Error("에러");
  }

  const rsData = await response.json();

  return (
    <div>
      <h1>글 목록</h1>

      <div>응답 코드 : {rsData.code}</div>
      <div>결과 메시지 : {rsData.msg}</div>

      <hr />

      <ul>
        <li>글1</li>
        <li>글2</li>
      </ul>
    </div>
  );
}
