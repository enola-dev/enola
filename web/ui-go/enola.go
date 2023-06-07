package main

import (
	"context"
	"fmt"

	"github.com/enola-dev/enola/ui-go/generated/dev/enola/core"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

type client struct {
	conn      *grpc.ClientConn
	svcClient core.EnolaServiceClient
}

func newClient(addr string) (*client, error) {
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		return nil, err
	}

	return &client{
		conn:      conn,
		svcClient: core.NewEnolaServiceClient(conn),
	}, nil
}

func (c *client) getEntity(ctx context.Context, id string) (*core.Entity, error) {
	resp, err := c.svcClient.GetEntity(ctx, &core.GetEntityRequest{
		Id: &core.ID{Entity: id},
	})
	if err != nil {
		return nil, fmt.Errorf("failed to call get entity endpoint: %w", err)
	}

	return resp.GetEntity(), nil
}

func (c *client) close() error {
	return c.conn.Close()
}
