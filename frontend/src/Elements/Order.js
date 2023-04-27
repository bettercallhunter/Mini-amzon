import React from "react";
import { Link } from "react-router-dom";
import authHeader from "../utils/authHeader";
import { Navigate } from "react-router-dom";
const Order = props => {
    const handleDelete = async (ev) => {
        ev.preventDefault();
        const response = await fetch("/api/cart/delete", {
            method: "DELETE",
            body: JSON.stringify({ order_id: props.id }),
            headers: { "Content-Type": "application/json", ...authHeader() },
            credentials: 'include'
        })
        if (response.ok) {
            alert("Item removed from cart")
            window.location.reload(false);
        }

    }


    return (
        <div>
            <h3>Order {props.id}</h3>
            <li>Item : {props.item.description}</li>
            <li>Price : {props.item.unitPrice}</li>
            {props.shipment && <li>Address : X: {props.shipment.destinationX} Y: {props.shipment.destinationY}</li>}
            {props.shipment && <li>ShipId : {props.shipment.id}</li>}
            <li>Quantity : {props.quantity}</li>
            <li>Status : {props.status}</li>
            <br />
            {props.status === "SHIPPINGCART" &&
                <Link to={`/delete/${props.id}`} onClick={handleDelete}>
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-6 h-6" width="1%">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                    </svg>

                    Remove from cart
                </Link>}
            {props.status === "PROCESSING" &&
                <Link to={`/editOrder/${props.id}`}>
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-6 h-6" width="1%">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                    </svg>

                    Change Address
                </Link>}
        </div >
    )
}
export default Order