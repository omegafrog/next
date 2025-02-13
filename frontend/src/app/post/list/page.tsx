export default async function PostList() {
  const response = await fetch("http://localhost:8080/api/v1/posts");

  if (!response.ok) {
    throw new Error("에러");
  }

  const rsData = await response.json();
  const pageDto = rsData.data;
  return (
    <div>
      <h1>글 목록</h1>

      <div>응답 코드 : {rsData.code}</div>
      <div>결과 메시지 : {rsData.msg}</div>

      <div>totalPages : {pageDto.totalPages}</div>
      <div>totalItems : {pageDto.totalItems}</div>
      <div>currentPageNo : {pageDto.currentPageNo}</div>
      <div>pageSize : {pageDto.pageSize}</div>
      <hr />

      <ul>
        <li>글1</li>
        <li>글2</li>
        {pageDto.items.map((item: PostDto) => {
          return (
            <li className="border-2 border-red-500 my-2 p-2" key={item.id}>
              <div>id : {item.id}</div>
              <div>title : {item.title}</div>
              <div>authorId : {item.authorId}</div>
              <div>authorName : {item.authorName}</div>
              <div>published : {item.published}</div>
              <div>listed : {item.listed}</div>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
type PostDto = {
  id: number;
  createDate: string;
  modifyDate: string;
  authorId: number;
  authorName: string;
  title: string;
  published: boolean;
  listed: boolean;
};

type PostItemPageDto = {
  currentPageNumber: number;
  pageSize: number;
  totalPages: number;
  totalItems: number;
  items: PostDto[];
};
