https://owasp.org/www-community/attacks/xss/
https://owasp.org/www-community/Types_of_Cross-Site_Scripting
https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html

# XSS - Persistent / Stored / Type II 

## HTML sanitization
The api-svc does not currently implement any form of HTML sanitization of the 
metadata fields that are minted.

## Output encoding

The mint operation is access controlled and can only be performed 
by authorized parties - but API clients should treat metadata field contents
as potentially containing malicious content. 

All clients are required to implement output-encoding on all RAiD metadata 
fields.  



