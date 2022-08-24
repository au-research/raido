
export function encodeBase64(plainText: string, encodeUri = false): string{
  let base64Encoded = btoa(plainText);
  if( !encodeUri ){
    return base64Encoded;
  }
  
  return encodeURIComponent(base64Encoded);
}