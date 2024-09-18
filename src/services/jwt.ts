import jwt, { JwtPayload } from 'jsonwebtoken'
const secret = require('../constants/constants')

interface User {
  username: string
  createdAt: Date
  updatedAt: Date
}

export const createToken = (user: User): string => {
  return jwt.sign(user, secret, { expiresIn: '1h' })
}

export const verifyToken = (token: string): JwtPayload | null => {
  try {
    return jwt.verify(token, secret) as JwtPayload
  } catch (error) {
    console.error('Invalid or expired token:', error)
    return null
  }
}
