-- CreateTable
CREATE TABLE "User" (
    "username" TEXT NOT NULL,
    "password" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,


CONSTRAINT "User_pkey" PRIMARY KEY ("username") );

-- CreateIndex
CREATE UNIQUE INDEX "User_password_key" ON "User" ("password");