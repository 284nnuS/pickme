#!/bin/sh
export PATH="$PATH:/home/nez21/.local/bin/"
sudo iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 3000
cd /home/nez21/pickme && yarn self:start