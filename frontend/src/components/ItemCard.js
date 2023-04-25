import React from "react";

const ItemCard = (props) => {
  const { id, name, description, unitPrice, seller, imgPath } = props;
  return (
    <div className="col">
      <div className="card">
        <img
          src="https://picsum.photos/600"
          className="card-img-top img-fluid rounded-cube"
          alt={name}
        />
        <div className="card-body">
          <h5 className="card-title">{name}</h5>
          <p className="card-text">{id}</p>
          <p className="card-text">{description}</p>
          <p className="card-text">{unitPrice}</p>
          <a href={`/buy/${id}`}>Buy</a>
        </div>
      </div>
    </div>
  );
};

export default ItemCard;
