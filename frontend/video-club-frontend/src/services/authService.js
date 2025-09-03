import axios from 'axios';

const API_URL = 'http://localhost:9090/api/auth';

class AuthService {
  login(username, password) {
    return axios
      .post(API_URL + '/login', {
        username,
        password
      })
      .then(response => {
        if (response.data.token) {
          localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
      });
  }

  register(username, email, password) {
    return axios.post(API_URL + '/register', {
      username,
      email,
      password
    });
  }

  logout() {
    localStorage.removeItem('user');
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));
  }

  getAuthHeader() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user && user.token) {
      return { Authorization: 'Bearer ' + user.token };
    }
    return {};
  }

  getUserProfile() {
  return axios.get(API_URL + '/profile', { 
    headers: this.getAuthHeader() 
  });
}

updateUserProfile(profileData) {
  return axios.put(API_URL + '/profile', profileData, { 
    headers: this.getAuthHeader() 
  });
}
}

export default new AuthService();