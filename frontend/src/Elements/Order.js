import React from "react";
import { Link } from "react-router-dom";
const Order = props => {
    const handleDelete = async (ev) => {
        ev.preventDefault();
        const response = await fetch(`http://localhost:8000/api/delete/${props.id}`, {
            method: "DELETE",
            headers: { "Content-Type": "application/json" },
            credentials: 'include'
        })
    }

    return (
        <div>
            <h3>Order {props.id}</h3>
            <li>Item : {props.item.description}</li>
            <li>Price : {props.item.unitPrice}</li>
            <li>Address : {props.address}</li>
            <li>Quantity : {props.quantity}</li>
            <li>Status : {props.status}</li>
            <br />
            <Link to={`/delete/${props.id}`} onClick={handleDelete}>
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-6 h-6" width="1%">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>

                Delete this post
            </Link>
        </div >
    )
}
export default Order