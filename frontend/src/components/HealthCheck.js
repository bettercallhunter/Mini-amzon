import React from "react";
import { useNavigate } from "react-router-dom";

const HealthCheck = () => {
  const navigate = useNavigate();
  const handleOnClick = async (e) => {
    e.preventDefault();
    const response = await fetch("/api/healthcheck", {
      method: "GET",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
    });
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
