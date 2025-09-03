import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from './authService';

const AuthGuard = ({ children }) => {
  const navigate = useNavigate();

  useEffect(() => {
    const user = authService.getCurrentUser();
    if (!user) {
      navigate('/login');
    }
  }, [navigate]);

  const user = authService.getCurrentUser();
  return user ? children : null;
};

export default AuthGuard;