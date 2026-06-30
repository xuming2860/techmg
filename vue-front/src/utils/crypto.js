import CryptoJS from 'crypto-js'

const SECRET_KEY = import.meta.env.VITE_USERINFO_ENCRYPT_KEY || 'techmg-default-key'

export function encrypt(data) {
  const json = JSON.stringify(data)
  return CryptoJS.AES.encrypt(json, SECRET_KEY).toString()
}

export function decrypt(ciphertext) {
  try {
    const bytes = CryptoJS.AES.decrypt(ciphertext, SECRET_KEY)
    const json = bytes.toString(CryptoJS.enc.Utf8)
    return JSON.parse(json)
  } catch (e) {
    console.warn('Failed to decrypt userInfo:', e.message)
    return null
  }
}
