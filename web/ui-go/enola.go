package main

import (
	"context"
	"fmt"

	enolagrpc "dev/enola/grpc"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

type client struct {
	conn      *grpc.ClientConn
	svcClient enolagrpc.EnolaServiceClient
}

func newClient(addr string) (*client, error) {
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		return nil, err
	}

	return &client{
		conn:      conn,
		svcClient: enolagrpc.NewEnolaServiceClient(conn),
	}, nil
}

func (c *client) getEntity(ctx context.Context, id string) (*enolagrpc.Entity, error) {
	resp, err := c.svcClient.GetEntity(ctx, &enolagrpc.GetEntityRequest{
		Id: &enolagrpc.ID{Entity: id},
	})
	if err != nil {
		return nil, fmt.Errorf("failed to call get entity endpoint: %w", err)
	}

	return resp.GetEntity(), nil
}

func (c *client) close() error {
	return c.conn.Close()
}
