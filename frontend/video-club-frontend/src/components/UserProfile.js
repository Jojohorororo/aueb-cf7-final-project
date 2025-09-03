import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/authService';
import './UserProfile.css';

const UserProfile = () => {
  const [user, setUser] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    loadUserProfile();
  }, []);

  const loadUserProfile = async () => {
    try {
      setLoading(true);
      const response = await authService.getUserProfile();
      setUser(response.data);
      setFormData({
        email: response.data.email || '',
        password: '',
        confirmPassword: ''
      });
    } catch (err) {
      setError('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (formData.password && formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    try {
      const updateData = {
        email: formData.email
      };
      
      if (formData.password) {
        updateData.password = formData.password;
      }

      await authService.updateUserProfile(updateData);
      setSuccess('Profile updated successfully!');
      setIsEditing(false);
      setFormData({ ...formData, password: '', confirmPassword: '' });
      
      // Reload profile to get updated data
      setTimeout(() => {
        loadUserProfile();
        setSuccess('');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to update profile');
    }
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  if (loading) return <div className="loading">Loading profile...</div>;

  return (
    <div className="profile-container">
      <div className="profile-card">
        <div className="profile-header">
          <h2>User Profile</h2>
          <button 
            onClick={() => navigate('/movies')} 
            className="btn btn-secondary"
          >
            Back to Movies
          </button>
        </div>

        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}

        <div className="profile-content">
          {isEditing ? (
            <form onSubmit={handleSave}>
              <div className="form-group">
                <label>Username:</label>
                <input
                  type="text"
                  value={user.username}
                  disabled
                  style={{ backgroundColor: '#f5f5f5' }}
                />
              </div>

              <div className="form-group">
                <label>Email:</label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="Enter email"
                />
              </div>

              <div className="form-group">
                <label>New Password (optional):</label>
                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Leave blank to keep current password"
                />
              </div>

              <div className="form-group">
                <label>Confirm New Password:</label>
                <input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="Confirm new password"
                />
              </div>

              <div className="profile-actions">
                <button type="submit" className="btn btn-primary">
                  Save Changes
                </button>
                <button 
                  type="button"
                  onClick={() => setIsEditing(false)} 
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
              </div>
            </form>
          ) : (
            <div className="profile-info">
              <div className="info-item">
                <label>Username:</label>
                <span>{user.username}</span>
              </div>

              <div className="info-item">
                <label>Email:</label>
                <span>{user.email || 'Not provided'}</span>
              </div>

              <div className="info-item">
                <label>Role:</label>
                <span className={`role-badge ${user.role?.toLowerCase()}`}>
                  {user.role}
                </span>
              </div>

              <div className="info-item">
                <label>Member Since:</label>
                <span>{new Date(user.createdAt).toLocaleDateString()}</span>
              </div>

              <div className="profile-actions">
                <button 
                  onClick={() => setIsEditing(true)} 
                  className="btn btn-primary"
                >
                  Edit Profile
                </button>
                
                <button onClick={handleLogout} className="btn btn-danger">
                  Logout
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default UserProfile;