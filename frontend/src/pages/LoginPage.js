import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Box,
  Button,
  Container,
  TextField,
  Typography,
  Paper,
  Alert,
  IconButton,
  InputAdornment,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import { Visibility, VisibilityOff, Email, Lock } from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import Logo from '../components/Logo';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      console.error('Login error:', err);
      if (err.response?.status === 403) {
        setError('Access forbidden. Please check your credentials and try again.');
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('Failed to login. Please try again later.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        background: `linear-gradient(135deg, ${theme.palette.primary.light} 0%, ${theme.palette.primary.main} 100%)`,
        padding: 2,
      }}
    >
      <Container component="main" maxWidth="sm">
        <Box
          sx={{
            minHeight: '100%',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <Paper
            elevation={24}
            sx={{
              p: 4,
              width: '100%',
              borderRadius: 2,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              background: 'rgba(255, 255, 255, 0.9)',
              backdropFilter: 'blur(10px)',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
            }}
          >
            <Box sx={{ mb: 4, transform: 'scale(1.2)' }}>
              <Logo size="large" color="primary" />
            </Box>

            <Typography
              component="h1"
              variant={isMobile ? 'h5' : 'h4'}
              sx={{
                mb: 3,
                fontWeight: 700,
                color: theme.palette.text.primary,
                textAlign: 'center',
              }}
            >
              Welcome!
            </Typography>

            {error && (
              <Alert
                severity="error"
                sx={{
                  width: '100%',
                  mb: 3,
                  borderRadius: 2,
                }}
              >
                {error}
              </Alert>
            )}

            <Box
              component="form"
              onSubmit={handleSubmit}
              sx={{
                width: '100%',
                '& .MuiTextField-root': {
                  mb: 2,
                },
              }}
            >
              <TextField
                required
                fullWidth
                label="Email Address"
                name="email"
                autoComplete="email"
                autoFocus
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={loading}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Email color="action" />
                    </InputAdornment>
                  ),
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                  },
                }}
              />

              <TextField
                required
                fullWidth
                name="password"
                label="Password"
                type={showPassword ? 'text' : 'password'}
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock color="action" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowPassword(!showPassword)}
                        edge="end"
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 2,
                  },
                }}
              />

              <Button
                type="submit"
                fullWidth
                variant="contained"
                disabled={loading}
                sx={{
                  mt: 2,
                  mb: 3,
                  py: 1.5,
                  borderRadius: 2,
                  fontSize: '1.1rem',
                  textTransform: 'none',
                  boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
                  '&:hover': {
                    transform: 'translateY(-1px)',
                    boxShadow: '0 6px 16px rgba(0, 0, 0, 0.2)',
                  },
                  transition: 'all 0.2s ease-in-out',
                }}
              >
                {loading ? 'Signing in...' : 'Sign In'}
              </Button>

              <Box
                sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: 1,
                }}
              >
                <Typography variant="body2" color="text.secondary">
                  Don't have an account?{' '}
                  <Link
                    to="/signup"
                    style={{
                      textDecoration: 'none',
                      color: theme.palette.primary.main,
                      fontWeight: 600,
                    }}
                  >
                    Sign Up
                  </Link>
                </Typography>
              </Box>
            </Box>
          </Paper>
        </Box>
      </Container>
    </Box>
  );
};

export default LoginPage; 