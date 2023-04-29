import React, { useEffect } from "react";
import { Link } from "react-router-dom";
import IconSearch from "../Templates/SearchButton";
import Pagination from "@mui/material/Pagination";
import ItemGroups from "../components/ItemGroups";
import NavBar from "../components/NavBar";
import HealthCheck from "../components/HealthCheck";
import LogoutButton from "../components/LogoutButton";

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
        searchName: "",
    });
    // const [searchTitle, setSearchTitle] = React.useState("");
    // const [currentPage, setCurrentPage] = React.useState(1);
    //   const

    const retriveItems = () => {
        const params = getRequestParams(items.searchName, items.number, items.size);
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
        params["search"] = items.searchName || "";
        params["page"] = items.number - 1;
        params["size"] = items.size;
        return params;
    };

    const handlePageChange = (event, value) => {
        setItems({ ...items, number: value });
    };  

    useEffect(() => {
        retriveItems();
        // console.log(items);
    }, [items.number, items.size]);

    // console.log(items.searchValue);
    return (
        <div className="container">
            <NavBar
                onSearch={(newValue) => setItems({ ...items, searchName: newValue })}
                onSubmit={(e) => {
                    e.preventDefault();
                    retriveItems();
                }}
            />
            <h1> Welcome</h1>
            <HealthCheck />
            <LogoutButton />

            <div className="form-floating mb-3">
                <select
                    className="form-control"
                    id="floatingSelect"
                    value={items.size}
                    onChange={(e) =>
                        setItems({ ...items, number: 1, size: e.target.value })
                    }
                    aria-label="number of item per page"
                >
                    <option value="4">4</option>
                    <option value="8">8</option>
                    <option value="12">12</option>
                </select>
                <label htmlFor="floatingSelect"># items per page</label>
            </div>
            <ItemGroups items={items.content} />

            <Link to={"/orders"}>
                <IconSearch />
                Check Orders
            </Link>
            <Link to={"/cart"}>
                Shopping Cart
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
