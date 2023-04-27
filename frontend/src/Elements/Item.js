import React from "react";
import { useState } from "react";

const Item = props => {
    return (
        <div>
            <img src={props.imgPath} />
            <li>Name: {props.name}</li>
            <li>Item : {props.description}</li>
            <li>Price : {props.unitPrice}</li>
            <li>Seller : {props.seller}</li>
            {props.categories.length > 0 && <li>categories: {props.categories[0].name}</li>}
            <a href={`/buy/${props.id}`}>Buy</a>
        </div>
    )

}
export default Item