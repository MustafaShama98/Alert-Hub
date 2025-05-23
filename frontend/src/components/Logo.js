import React from 'react';
import { Box, Typography } from '@mui/material';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';

const Logo = ({ size = 'medium', showText = true, color = 'primary' }) => {
  const getIconSize = () => {
    switch (size) {
      case 'small':
        return { icon: 24, text: '1rem' };
      case 'large':
        return { icon: 48, text: '1.75rem' };
      default: // medium
        return { icon: 36, text: '1.5rem' };
    }
  };

  const dimensions = getIconSize();

  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        gap: 1,
      }}
    >
      <NotificationsActiveIcon
        color={color}
        sx={{
          fontSize: dimensions.icon,
          animation: 'pulse 2s infinite',
          '@keyframes pulse': {
            '0%': {
              transform: 'scale(1)',
            },
            '50%': {
              transform: 'scale(1.1)',
            },
            '100%': {
              transform: 'scale(1)',
            },
          },
        }}
      />
      {showText && (
        <Typography
          variant="h6"
          color={color}
          sx={{
            fontSize: dimensions.text,
            fontWeight: 700,
            letterSpacing: '0.05em',
            fontFamily: '"Poppins", sans-serif',
          }}
        >
          Alert Hub
        </Typography>
      )}
    </Box>
  );
};

export default Logo; 