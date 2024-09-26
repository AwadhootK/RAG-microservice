import { Router } from 'express'
import {
  deleteUser,
  login,
  logout,
  signup
} from '../controllers/authControllers'

const authRouter = Router()

authRouter.post('/signup', signup)

authRouter.post('/login', login)

authRouter.get('/logout', logout)

authRouter.delete('/delete', deleteUser)

export default authRouter
