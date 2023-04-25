import React from "react";
import ItemCard from "./ItemCard";

const ItemGroups = (props) => {
  const items = props.items;
  return (
    <div className="row row-cols-1 row-cols-sm-2 row-cols-md-4 g-4">
      {items.map((items, index) => (
        <ItemCard {...items} key={`card-${index}`} />
      ))}
    </div>
  );
};
export default ItemGroups;
