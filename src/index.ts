import cors from 'cors'
import express, { Request, Response } from 'express'
import authRouter from './routes/authRoutes'

const app = express()

// var options = {
// origin: 'http://localhost:8080'
// }

app.use(cors())

app.use(express.json())

app.use((req: Request, res: Response, next) => {
  console.log(`Request received: ${req.method} ${req.url}`)
  if (req.body) {
    console.log(`Request body: ${JSON.stringify(req.body)}`)
  }
  next()
})

app.get('/ping', (req: Request, res: Response) => {
  res.json('pong')
})

app.use('/', authRouter)

const PORT = process.env.PORT || 8500

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`)
})
