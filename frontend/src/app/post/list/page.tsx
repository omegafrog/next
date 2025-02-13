export default async function PostList({
  searchParams,
}: {
  searchParams: { keywordType: string; keyword: string };
}) {
  const { keywordType = "title", keyword = "" } = await searchParams;

  const response = await fetch(
    `http://localhost:8080/api/v1/posts?keyword-type=${keywordType}&keyword=${keyword}`
  );

  if (!response.ok) {
    throw new Error("에러");
  }

  const rsData = await response.json();
  const pageDto: PostItemPageDto = rsData.data;
  return (
    <div>
      <h1>글 목록</h1>

      <div>응답 코드 : {rsData.code}</div>
      <div>결과 메시지 : {rsData.msg}</div>

      <div>totalPages : {pageDto.totalElementSize}</div>
      <div>totalItems : {pageDto.totalElementSize}</div>
      <div>currentPageNo : {pageDto.currentPageNum}</div>
      <div>pageSize : {pageDto.pageSize}</div>

      <form>
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
      <hr />

      <ul>
        <li>글1</li>
        <li>글2</li>
        {pageDto.items?.map((item: PostDto) => {
          return (
            <li className="border-2 border-red-500 my-2 p-2" key={item.id}>
              <div>id : {item.id}</div>
              <div>title : {item.title}</div>
              <div>authorId : {item.authorId}</div>
              <div>authorName : {item.authorName}</div>
              <div>published : {item.opened}</div>
              <div>listed : {item.listed}</div>
            </li>
          );
        })}
      </ul>
    </div>
  );
}

type PostDto = components["schemas"]["PostDto"];

type PostItemPageDto = components["schemas"]["PageDto"];
