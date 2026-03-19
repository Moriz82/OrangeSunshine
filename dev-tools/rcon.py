#!/usr/bin/env python3
"""Minimal RCON client. Stdlib only."""

import socket
import struct
import sys
import time

SERVERDATA_AUTH = 3
SERVERDATA_EXECCOMMAND = 2
SERVERDATA_AUTH_RESPONSE = 2
SERVERDATA_RESPONSE_VALUE = 0


def _pack(req_id: int, ptype: int, body: str) -> bytes:
    payload = body.encode("utf-8") + b"\x00\x00"
    header = struct.pack("<ii", req_id, ptype)
    return struct.pack("<i", len(header) + len(payload)) + header + payload


def _unpack(data: bytes):
    size = struct.unpack("<i", data[:4])[0]
    req_id, ptype = struct.unpack("<ii", data[4:12])
    body = data[12 : 4 + size - 2].decode("utf-8", errors="replace")
    return req_id, ptype, body


class RconClient:
    def __init__(self, host: str = "127.0.0.1", port: int = 25575, password: str = "", timeout: float = 10.0, retries: int = 5):
        last_exc = None
        for attempt in range(retries):
            try:
                self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                self.sock.settimeout(timeout)
                self.sock.connect((host, port))
                self._req_id = 1
                self._auth(password)
                return
            except (ConnectionRefusedError, OSError) as e:
                last_exc = e
                time.sleep(2)
        raise last_exc

    def _auth(self, password: str):
        self.sock.sendall(_pack(self._req_id, SERVERDATA_AUTH, password))
        data = self._recv()
        req_id, ptype, _ = _unpack(data)
        if req_id == -1:
            raise ConnectionRefusedError("RCON authentication failed — wrong password?")

    def _recv(self) -> bytes:
        raw = b""
        # read length prefix
        while len(raw) < 4:
            raw += self.sock.recv(4 - len(raw))
        size = struct.unpack("<i", raw[:4])[0]
        while len(raw) < 4 + size:
            raw += self.sock.recv(4 + size - len(raw))
        return raw

    def command(self, cmd: str) -> str:
        self._req_id += 1
        self.sock.sendall(_pack(self._req_id, SERVERDATA_EXECCOMMAND, cmd))
        _, _, body = _unpack(self._recv())
        return body

    def close(self):
        self.sock.close()


def main():
    import argparse

    parser = argparse.ArgumentParser(description="Send an RCON command and print the response")
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=25575)
    parser.add_argument("--password", default="devtest")
    parser.add_argument("command", nargs="+")
    args = parser.parse_args()

    client = RconClient(args.host, args.port, args.password)
    resp = client.command(" ".join(args.command))
    print(resp)
    client.close()


if __name__ == "__main__":
    main()
