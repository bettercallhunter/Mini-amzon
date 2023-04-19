import './App.css';
import React from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import { Routes } from 'react-router-dom';
import Index from './Pages/Index';
import Login from './User/Login';
import Register from './User/Register';
import Buy from './Pages/Buy';
import Orders from './Pages/Orders';
function App() {
  return (
    <Router>
      <Routes>

        <Route exact path="/" element={<Index />} />
        <Route exact path="/login" element={<Login />} />
        <Route exact path="/register" element={<Register />} />
        <Route exact path="/buy/:id" element={<Buy />} />
        <Route exact path="/orders" element={<Orders />} />

      </Routes>
    </Router>
  );
}

export default App;
