import React, { useEffect } from "react"
import logo from '../logo.svg';
import Item from "../Item";

const Index = () => {
    const [items, setItems] = React.useState([]);
    useEffect(() => {
        const fetchItem = async () => {
            const response = await fetch('http://localhost:8000', {
                method: "GET",
                headers: { "Content-Type": "application/json" },
                credentials: 'include'
            })
            const item = await response.json();
            setItems(item);
        }
        fetchItem();
    }, [])

    console.log(items)
    return (
        <React.Fragment>
            < h1 > Welcome</h1>
            {items.length >= 1 && items.map(items => <Item {...items} />)}
        </React.Fragment >


    )
}
export default Index 