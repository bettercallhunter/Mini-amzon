import './App.css';
import React from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import { Routes } from 'react-router-dom';
import Index from './Pages/index';
import Login from './User/Login';
import Register from './User/Register';
import Buy from './Pages/Buy';
function App() {
  return (
    <Router>
      <Routes>

        <Route exact path="/" element={<Index />} />
        <Route exact path="/login" element={<Login />} />
        <Route exact path="/register" element={<Register />} />
        <Route exact path={"/buy/:id"} element={<Buy />} />

      </Routes>
    </Router>
  );
}

export default App;
