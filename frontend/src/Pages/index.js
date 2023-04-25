import React, { useEffect } from "react";
import { Link } from "react-router-dom";
import IconSearch from "../Templates/SearchButton";
import Pagination from "@mui/material/Pagination";
import ItemGroups from "../components/ItemGroups";
import NavBar from "../components/NavBar";

const Index = () => {
  const [items, setItems] = React.useState({
    content: [],
    empty: true,
    first: true,
    last: false,
    number: 1,
    numberOfElements: 0,
    size: 4,
    totalElements: 0,
    totalPages: 0,
  });
  const [searchTitle, setSearchTitle] = React.useState("");
  // const [currentPage, setCurrentPage] = React.useState(1);
  //   const
  useEffect(() => {
    retriveItems();
    console.log(items);
  }, [items.number, items.size]);

  const retriveItems = () => {
    const params = getRequestParams(searchTitle, items.number, items.size);
    const fetchItem = async () => {
      const response = await fetch(
        "/api/items?" +
          new URLSearchParams({
            ...params,
          }),
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
        }
      );
      const result = await response.json();
      // console.log(items);
      setItems({ ...result, number: result.number + 1 });
    };
    fetchItem();
  };

  const getRequestParams = () => {
    let params = {};
    // if (searchTitle) {
    //   params["title"] = searchTitle;
    // }
    params["page"] = items.number - 1;
    params["size"] = items.size;
    return params;
  };

  const handlePageChange = (event, value) => {
    setItems({ ...items, number: value });
  };

  return (
    <div className="container">
      <NavBar />
      <h1> Welcome</h1>

      <div class="form-floating mb-3">
        <select
          className="form-control"
          id="floatingSelect"
          onChange={(e) =>
            setItems({ ...items, number: 1, size: e.target.value })
          }
          aria-label="number of item per page"
        >
          <option value="4" selected>
            4
          </option>
          <option value="8">8</option>
          <option value="12">12</option>
        </select>
        <label for="floatingSelect"># items per page</label>
      </div>
      <ItemGroups items={items.content} />

      <Link to={"/orders"}>
        <IconSearch />
        Check Orders
      </Link>
      <Pagination
        count={items.totalPages}
        page={items.number}
        siblingCount={2}
        boundaryCount={2}
        variant="outlined"
        shape="rounded"
        color="primary"
        onChange={handlePageChange}
      />
    </div>
  );
};
export default Index;
