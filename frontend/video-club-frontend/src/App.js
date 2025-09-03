import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import MovieList from './components/MovieList';
import MovieForm from './components/MovieForm';
import AuthGuard from './services/authGuard';
import authService from './services/authService';
import UserProfile from './components/UserProfile';
import './App.css';

function App() {
  const currentUser = authService.getCurrentUser();

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route 
  path="/profile" 
  element={
    <AuthGuard>
      <UserProfile />
    </AuthGuard>
  } 
/>
          <Route 
            path="/" 
            element={currentUser ? <Navigate to="/movies" /> : <Navigate to="/login" />} 
          />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route 
            path="/movies" 
            element={
              <AuthGuard>
                <MovieList />
              </AuthGuard>
            } 
          />
          <Route 
            path="/movies/new" 
            element={
              <AuthGuard>
                <MovieForm />
              </AuthGuard>
            } 
          />
          <Route 
            path="/movies/edit/:id" 
            element={
              <AuthGuard>
                <MovieForm />
              </AuthGuard>
            } 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;