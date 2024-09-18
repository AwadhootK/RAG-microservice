export const passwordCheck = (password: string): string => {
  if (password.length < 8) {
    return 'Password must be at least 8 characters long'
  }

  if (!password.match(/[a-z]/)) {
    return 'Password must contain at least one lowercase letter'
  }

  if (!password.match(/[A-Z]/)) {
    return 'Password must contain at least one uppercase letter'
  }

  if (!password.match(/\d/)) {
    return 'Password must contain at least one number'
  }

  if (!password.match(/[@$!%*?&#]/)) {
    return 'Password must contain at least one special character (@, $, !, %, *, ?, &, #)'
  }
  return 'Password is valid'
}
