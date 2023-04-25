import React from "react";
import ItemCard from "./ItemCard";

const ItemGroups = (props) => {
  const items = props.items;
  return (
    <div class="row row-cols-1 row-cols-sm-2 row-cols-md-4 g-4">
      {items.map((items) => (
        <ItemCard {...items} />
      ))}
    </div>
  );
};
export default ItemGroups;
