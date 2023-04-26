import React from "react";
import { useNavigate } from "react-router-dom";
import authHeader from "../utils/authHeader";

const HealthCheck = () => {
  const navigate = useNavigate();
  const handleOnClick = async (e) => {
    e.preventDefault();
    const header = { 
        "Content-Type": "application/json",
      ...authHeader() };

    const response = await fetch("/api/health", 
        {
      method: "GET",
      credentials: "include",
      headers:header
       }
     
    // }
    );
    // const data = await response.json();
    // console.log(data);
    // console.log({ "Content-Type": "application/json",
    // ...authHeader() })
    if (response.status === 200) {
      alert("healthcheck successful");
    } else {
      alert("healthcheck failed, plz sign in ");
      navigate("/login");
    }
  };
  return (
    <button type="button" className="btn btn-primary" onClick={handleOnClick}>
      Health Check Button
    </button>
  );
};

export default HealthCheck;
