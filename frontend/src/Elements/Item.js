import React from "react";
import { useState } from "react";

const Item = props => {
    return (
        <div>
            <img src={props.imgPath} />
            <li>Item : {props.description}</li>
            <li>Price : {props.unitPrice}</li>
            <li>Seller : {props.seller}</li>
            <a href={`/buy/${props.id}`}>Buy</a>
        </div>
    )

}
export default Item