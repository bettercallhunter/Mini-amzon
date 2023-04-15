import React from "react";
import { Link } from "react-router-dom";
const Order = props => {
    return (
        <div>
            <li>Item : {props.description}</li>
            <li>Price : {props.unitPrice}</li>


            <li>Quantity : {props.quantity}</li>
            <br />


        </div>
    )
}
export default Order