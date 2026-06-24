import { describe, it, expect, beforeEach } from 'vitest';
import { getUserId } from './apiClient';

describe('getUserId', () => {
    beforeEach(() => {
        sessionStorage.clear();
    });

    it('throws error when no token is present', () => {
        expect(() => getUserId()).toThrow('No authentication token found');
    });

    it('throws error when token is malformed', () => {
        sessionStorage.setItem('jwt_token', 'invalid.token');
        expect(() => getUserId()).toThrow('Malformed token');
    });

    it('throws error when token has expired', () => {
        // Create an expired token payload
        const payload = { sub: 'user-123', exp: Math.floor(Date.now() / 1000) - 3600 };
        const base64Url = btoa(JSON.stringify(payload)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
        sessionStorage.setItem('jwt_token', `header.${base64Url}.signature`);
        
        expect(() => getUserId()).toThrow('Token has expired');
    });

    it('successfully extracts userId from valid token', () => {
        const payload = { sub: 'user-123', exp: Math.floor(Date.now() / 1000) + 3600 };
        const base64Url = btoa(JSON.stringify(payload)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
        sessionStorage.setItem('jwt_token', `header.${base64Url}.signature`);
        
        expect(getUserId()).toBe('user-123');
    });
});
