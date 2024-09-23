import { Request, Response } from 'express'
import prisma from '../constants/prismaClient'
import { comparePassword, hashPassword } from '../services/hashing'
import { createToken, verifyToken } from '../services/jwt'
import { passwordCheck } from '../services/passwordCheck'

export const signup = async (req: Request, res: Response) => {
  console.log('Signup request received')
  const { username, password } = req.body

  // Check if username and password are provided
  if (!username || !password) {
    return res.status(400).json({ error: 'Username and password are required' })
  }

  // Validate the password
  const passwordCheckResponse = passwordCheck(password)
  if (passwordCheckResponse !== 'Password is valid') {
    return res.status(400).json({ error: passwordCheckResponse })
  }

  try {
    // Hash the password
    const hashedPassword = await hashPassword(password)

    // Create a new user in the database
    const user = await prisma.user.create({
      data: {
        username: username,
        password: hashedPassword
      }
    })

    // Generate a JWT token
    const jwt = createToken({
      username: user.username,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt
    })

    // Set the JWT token in a cookie
    res.cookie('jwt', jwt, {
      httpOnly: true,
      secure: true,
      sameSite: 'none'
    })

    // Send the user object without sensitive fields like password
    res.status(200).json({
      username: user.username,
      token: jwt
    })
  } catch (error) {
    // Handle errors gracefully
    console.error('Error during signup:', error)
    res.status(500).json({ error: 'An error occurred during signup' })
  }
}

export const login = async (req: Request, res: Response) => {
  console.log('Login request received')
  const { username, password } = req.body

  if (!username || !password) {
    return res.status(400).json({ error: 'Username and password are required' })
  }

  try {
    // Find the user by username
    const user = await prisma.user.findUnique({
      where: { username }
    })

    if (!user) {
      return res.status(400).json({ error: 'Invalid username or password' })
    }

    // Check if the password matches
    const isPasswordValid = await comparePassword(password, user.password)

    if (!isPasswordValid) {
      return res.status(400).json({ error: 'Invalid username or password' })
    }

    const jwt = createToken({
      username: user.username,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt
    })

    res.cookie('jwt', jwt, {
      httpOnly: true,
      secure: true,
      sameSite: 'none'
    })

    return res.status(200).json({
      username: user.username,
      token: jwt
    })
  } catch (error) {
    console.error('Error during login:', error)
    return res.status(500).json({ error: 'An error occurred during login' })
  }
}

export const logout = (req: Request, res: Response) => {
  // Clear the JWT cookie
  if (!req.cookies || !req.cookies.jwt) {
    return res.status(400).json({ error: 'User is not logged in' })
  }

  res.clearCookie('jwt')

  // Send a success response
  res.status(200).json({ message: 'Logged out successfully' })
}

export const deleteUser = async (req: Request, res: Response) => {
  if (!req.cookies || !req.cookies.jwt) {
    return res.status(400).json({ error: 'JWT token missing' })
  }

  const token = req.cookies.jwt

  try {
    // Verify the JWT token
    const decodedToken = verifyToken(token)

    if (!decodedToken || typeof decodedToken !== 'object') {
      return res.status(400).json({ error: 'Invalid or expired JWT token' })
    }

    // Ensure the decoded token has a username
    const { username } = decodedToken
    if (!username) {
      return res.status(400).json({ error: 'Invalid JWT payload' })
    }

    // Find the user in the database
    const user = await prisma.user.findUnique({
      where: { username }
    })

    if (!user) {
      return res.status(404).json({ error: 'User not found' })
    }

    // Delete the user from the database
    await prisma.user.delete({
      where: { username }
    })

    return res.status(200).json({ message: 'User deleted successfully' })
  } catch (error) {
    console.error('Error during user deletion:', error)
    return res
      .status(500)
      .json({ error: 'An error occurred during user deletion' })
  }
}
