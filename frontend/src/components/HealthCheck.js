import React from "react";
import { useNavigate } from "react-router-dom";
import authHeader from "../utils/authHeader";

const HealthCheck = () => {
  const navigate = useNavigate();
  const handleOnClickGet = async (e) => {
    e.preventDefault();
    const header = {
      "Content-Type": "application/json",
      ...authHeader(),
    };

    const response = await fetch("/api/health", {
      method: "GET",
      credentials: "include",
      headers: header,
    });
    if (response.status === 200) {
      alert("healthcheck successful");
    } else {
      alert("healthcheck failed, plz sign in ");
      navigate("/login");
    }
  };

  const handleOnClickPost = async (e) => {
    e.preventDefault();
    const header = {
      "Content-Type": "application/json",
      ...authHeader(),
    };

    const response = await fetch("/api/health", {
      method: "Post",
      credentials: "include",
      headers: header,
    });
    if (response.status === 200) {
      alert("healthcheck successful");
    } else {
      alert("healthcheck failed, plz sign in ");
      navigate("/login");
    }
  };

  const handleOnClickEmail = async (e) => {
    e.preventDefault();
    const header = {
      "Content-Type": "application/json",
      ...authHeader(),
    };

    const response = await fetch("/api/emailCheck", {
      method: "Post",
      credentials: "include",
      headers: header,
    });
    if (response.status === 200) {
      alert("healthcheck successful");
    } else {
      alert("healthcheck failed, plz sign in ");
      navigate("/login");
    }
  };
  return (
    <div>
      <button
        type="button"
        className="btn btn-primary"
        onClick={handleOnClickGet}
      >
        Health Check Button (Get)
      </button>
      <button
        type="button"
        className="btn btn-primary"
        onClick={handleOnClickPost}
      >
        Health Check Button (Post)
      </button>
      <button
        type="button"
        className="btn btn-primary"
        onClick={handleOnClickEmail}
      >
        Email Check Button (Post)
      </button>
    </div>
  );
};

export default HealthCheck;
