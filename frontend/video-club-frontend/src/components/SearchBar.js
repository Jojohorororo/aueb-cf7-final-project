// src/components/SearchBar.js
import React, { useState } from 'react';
import './SearchBar.css';

const SearchBar = ({ onSearch, onReset }) => {
  const [searchParams, setSearchParams] = useState({
    title: '',
    genre: '',
    director: '',
    year: ''
  });

  const handleChange = (e) => {
    setSearchParams({
      ...searchParams,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Filter out empty values
    const filteredParams = Object.keys(searchParams).reduce((acc, key) => {
      if (searchParams[key]) {
        acc[key] = searchParams[key];
      }
      return acc;
    }, {});
    
    onSearch(filteredParams);
  };

  const handleReset = () => {
    setSearchParams({
      title: '',
      genre: '',
      director: '',
      year: ''
    });
    onReset();
  };

  return (
    <div className="search-bar">
      <form onSubmit={handleSubmit} className="search-form">
        <div className="search-fields">
          <input
            type="text"
            name="title"
            placeholder="Search by title..."
            value={searchParams.title}
            onChange={handleChange}
          />
          <input
            type="text"
            name="genre"
            placeholder="Genre..."
            value={searchParams.genre}
            onChange={handleChange}
          />
          <input
            type="text"
            name="director"
            placeholder="Director..."
            value={searchParams.director}
            onChange={handleChange}
          />
          <input
            type="number"
            name="year"
            placeholder="Year..."
            value={searchParams.year}
            onChange={handleChange}
            min="1900"
            max="2030"
          />
        </div>
        <div className="search-buttons">
          <button type="submit" className="btn btn-search">Search</button>
          <button type="button" onClick={handleReset} className="btn btn-reset">Reset</button>
        </div>
      </form>
    </div>
  );
};

export default SearchBar;