import React, { useEffect } from "react"
import logo from '../logo.svg';
import Item from "../Elements/Item";
import { Navigate } from "react-router-dom";
import { Link } from 'react-router-dom';
import IconSearch from "../Templates/SearchButton";

const Index = () => {
    const [items, setItems] = React.useState([]);
    useEffect(() => {
        const fetchItem = async () => {
            const response = await fetch('/api/items', {
                method: "GET",
                headers: { "Content-Type": "application/json" },
                credentials: 'include'
            })
            const item = await response.json();
            setItems(item.content);
        }
        fetchItem();
    }, [])

    console.log(items)
    return (
        <React.Fragment>
            < h1 > Welcome</h1>

            {items.length >= 1 && items.map(items => <Item {...items} />)}

            <Link to={"/orders"}>
                <IconSearch />
                Check Orders
            </Link>
            <Link to={"/findorder"}>
                <IconSearch />
                Find Orders
            </Link>
        </React.Fragment >


    )
}
export default Index 